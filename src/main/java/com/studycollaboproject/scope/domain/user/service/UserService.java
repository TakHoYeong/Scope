package com.studycollaboproject.scope.domain.user.service;

import com.studycollaboproject.scope.domain.post.repository.BookmarkRepository;
import com.studycollaboproject.scope.domain.user.dto.LoginReponseDto;
import com.studycollaboproject.scope.domain.user.dto.SnsInfoDto;
import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.post.model.ProjectStatus;
import com.studycollaboproject.scope.domain.team.model.Team;
import com.studycollaboproject.scope.domain.post.repository.PostRepository;
import com.studycollaboproject.scope.domain.team.repository.TeamRepository;
import com.studycollaboproject.scope.domain.post.repository.TechStackRepository;
import com.studycollaboproject.scope.domain.user.dto.UserRequestDto;
import com.studycollaboproject.scope.domain.user.dto.UserResponseDto;
import com.studycollaboproject.scope.domain.post.model.TechStack;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.domain.applicant.repository.ApplicantRepository;
import com.studycollaboproject.scope.domain.user.repository.UserRepository;
import com.studycollaboproject.scope.global.common.ResponseDto;
import com.studycollaboproject.scope.global.error.exception.BadRequestException;
import com.studycollaboproject.scope.global.error.exception.ErrorCode;
import com.studycollaboproject.scope.global.security.JwtTokenProvider;
import com.studycollaboproject.scope.global.util.TechStackConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.ArrayList;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TechStackConverter techStackConverter;
    private final TechStackRepository techStackRepository;
    private final PostRepository postRepository;
    private final ApplicantRepository applicantRepository;
    private final TeamRepository teamRepository;

    //기술스택 리스트와 유저 정보를 같이 DB에 저장
    public UserResponseDto saveUser(List<String> techStack, User user) {
        Set<String> techStackStringList = new HashSet<>(techStack);
        List<TechStack> techStackList = techStackConverter.convertStringToTechStack(new ArrayList<>(techStackStringList), user, null);
        user.addTechStackList(techStackList);
        User savedUser = userRepository.save(user);
        techStackRepository.saveAll(techStackList);
        return new UserResponseDto(savedUser, techStackConverter.convertTechStackToString(user.getTechStackList()));
    }

    // sns id로 user 정보 반환
    public User loadUserBySnsId(String snsId) {
        return userRepository.findBySnsId(snsId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_USER_ERROR));
    }

    //user id로 정보 반환
    public User loadUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(ErrorCode.NO_USER_ERROR));
    }

    public User loadUnknownUser() {
        return userRepository.findByUserPropensityType("unknown");
    }

    //user sns id로 JWT토큰 생성 후 반환
    public String createToken(User user) {
        return jwtTokenProvider.createToken(user.getSnsId());
    }

    //email 중복 체크
    public boolean emailCheckByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    //닉네임 중복 체크
    public boolean nicknameCheckByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    //sns 로그인 시 기존 회원 여부 판단
    public ResponseDto SignupUserCheck(String snsId, String sns) {
        String id = sns + snsId;
        User user = userRepository.findBySnsId(id).orElse(null);

        if (user == null) {
            return new ResponseDto("추가 정보 작성이 필요한 사용자입니다.", new SnsInfoDto(id));
        } else {
            LoginReponseDto loginReponseDto = new LoginReponseDto(jwtTokenProvider.createToken(id), user.getNickname(), user.getId());
            return new ResponseDto("로그인이 완료되었습니다", loginReponseDto);
        }
    }

    public List<UserResponseDto> adminUserPropensityType(){
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        List<User> allUser = userRepository.findAll();
        for (User user : allUser) {
            UserResponseDto userResponseDto = new UserResponseDto(user);
            userResponseDtos.add(userResponseDto);
        }
        return userResponseDtos;
    }

    //email, nickname, 기술스택 수정
    @Transactional
    public UserResponseDto updateUserInfo(String snsId, UserRequestDto userRequestDto) {

        String nickname = userRequestDto.getNickname();
        User user = loadUserBySnsId(snsId);

        techStackRepository.deleteAllByUser(user);
        user.resetTechStack();

        if (!user.getNickname().equals(nickname)) {
            nicknameCheckByNickname(nickname);
        }

        Set<String> techStackStringList = new HashSet<>(userRequestDto.getUserTechStack());
        List<TechStack> techStackList = techStackConverter.convertStringToTechStack(new ArrayList<>(techStackStringList), user, null);
        user.resetTechStack();
        user.updateUserInfo(nickname, techStackList);
        techStackRepository.saveAll(techStackList);
        return new UserResponseDto(user, techStackConverter.convertTechStackToString(user.getTechStackList()));
    }

    // 회원 소개 수정
    @Transactional
    public UserResponseDto updateUserDesc(String snsId, String userDesc) {
        User user = loadUserBySnsId(snsId);
        user.updateUserDesc(userDesc);

        return new UserResponseDto(user, techStackConverter.convertTechStackToString(user.getTechStackList()));
    }

    @Transactional
    public ResponseDto deleteUser(User user) {

        techStackRepository.deleteAllByUser(user);
        applicantRepository.deleteAllByUser(user);
        bookmarkRepository.deleteAllByUser(user);
        List<Team> teamList = teamRepository.findAllByUser(user);
        for (Team team : teamList) {
            team.deleteTeam();
            teamRepository.delete(team);
        }

        List<Post> postList = postRepository.findAllByUser(user);

        // 탈퇴하려는 유저의 Post 중 상태가 종료가 아닌 Post를 삭제
        for (Post post : postList) {
            if (post.getProjectStatus().equals(ProjectStatus.PROJECT_STATUS_END)) {
                post.deleteUser(loadUnknownUser());
            } else {
                techStackRepository.deleteAllByPost(post);
                teamRepository.deleteAllByPost(post);
                bookmarkRepository.deleteAllByPost(post);
                applicantRepository.deleteAllByPost(post);
                postRepository.delete(post);
            }
        }
        userRepository.delete(user);
        return new ResponseDto("성공적으로 회원 정보가 삭제되었습니다.", "");
    }

    @Transactional
    public User setEmailAuthCode(String snsId) {
        User user = userRepository.findBySnsId(snsId).orElseThrow(() -> new BadRequestException(ErrorCode.NO_USER_ERROR));
        user.setmailAuthenticationCode();
        return user;

    }


}
