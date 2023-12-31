package com.studycollaboproject.scope.domain.team.model;

import com.studycollaboproject.scope.domain.post.model.Post;
import com.studycollaboproject.scope.domain.user.model.User;
import com.studycollaboproject.scope.global.util.Timestamped;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Team extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    private boolean isAssessment;

    @Builder
    public Team(User user, Post post){
        this.post = post;
        this.user = user;
        post.updateMember();
        this.isAssessment = false;
    }

    public void setAssessment(){
        this.isAssessment = true;
    }

    public void setUrl(String frontUrl, String backUrl){
        this.post.setUrl(frontUrl,backUrl);
    }

    public void deleteTeam() {
        this.post.deleteMember();
        this.post.getTeamList().remove(this);
        this.user.getTeamList().remove(this);
    }
}
