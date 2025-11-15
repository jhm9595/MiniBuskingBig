package com.minibuskingbig.team.repository;

import com.minibuskingbig.singer.entity.SingerProfile;
import com.minibuskingbig.team.entity.Team;
import com.minibuskingbig.team.entity.TeamMember;
import com.minibuskingbig.team.entity.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamAndIsActiveTrue(Team team);

    List<TeamMember> findBySingerAndIsActiveTrue(SingerProfile singer);

    Optional<TeamMember> findByTeamAndSinger(Team team, SingerProfile singer);

    boolean existsByTeamAndSingerAndIsActiveTrue(Team team, SingerProfile singer);

    List<TeamMember> findByTeamAndRole(Team team, TeamRole role);
}
