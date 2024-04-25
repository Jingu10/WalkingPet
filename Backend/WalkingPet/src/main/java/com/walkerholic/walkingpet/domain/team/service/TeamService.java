package com.walkerholic.walkingpet.domain.team.service;

import com.walkerholic.walkingpet.domain.team.dto.request.CreateGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.JoinGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamResponse;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamUsersResponse;
import com.walkerholic.walkingpet.domain.team.entity.Team;
import com.walkerholic.walkingpet.domain.team.entity.TeamUser;
import com.walkerholic.walkingpet.domain.team.repository.TeamRepository;
import com.walkerholic.walkingpet.domain.team.repository.TeamUserRepository;
import com.walkerholic.walkingpet.domain.users.entity.UserDetail;
import com.walkerholic.walkingpet.domain.users.entity.UserStep;
import com.walkerholic.walkingpet.domain.users.entity.Users;
import com.walkerholic.walkingpet.domain.users.repository.UserDetailRepository;
import com.walkerholic.walkingpet.domain.users.repository.UserStepRepository;
import com.walkerholic.walkingpet.domain.users.repository.UsersRepository;
import com.walkerholic.walkingpet.global.error.GlobalBaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.walkerholic.walkingpet.global.error.GlobalErrorCode.TEAM_NOT_FOUND;
import static com.walkerholic.walkingpet.global.error.GlobalErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private static final int MAX_ALLOWED_TEAMS  = 3;
    private static final int MAX_TEAM_PEOPLE  = 6;

    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UsersRepository usersRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserStepRepository userStepRepository;

    @Transactional
    public List<TeamResponse> getAllTeam() {
        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) {
            // 현재 등록된 그룹이 없으면 빈 배열 반환
            return Collections.emptyList();
        } else {
            return getTeamResponses(teams);
        }
    }

    @Transactional
    public List<TeamResponse> getUserTeams(int userId) {

        Users user = usersRepository.findUsersByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));

        List<Team> teams = teamUserRepository.findTeamsByUser(user);

        if (teams.isEmpty()) {
            // 사용자가 가입한 그룹이 없으면 빈 배열 반환
            return Collections.emptyList();
        } else {
            return getTeamResponses(teams);
        }

    }
    @Transactional
    public List<TeamResponse> getSearchTeams(String content) {

        List<Team> teams = teamRepository.findByNameContaining(content);

        if (teams.isEmpty()) {
            // 검색 결과에 맞는 팀이 없으면 빈 배열 반환
            return Collections.emptyList();
        } else {
            return getTeamResponses(teams);
        }
    }

    // Team 객체에 userCount를 추가해서 TeamResponse 형식에 맞추는 메소드
    private List<TeamResponse> getTeamResponses(List<Team> teams) {
        return teams.stream()
                .map(team -> {
                    Integer userCount = teamUserRepository.countByTeamId(team.getTeamId());
                    return TeamResponse.from(team, userCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void joinGroup(JoinGroupRequest joinGroupRequest,int userId) {

        // 사용자 검색 및 없을 경우 예외 발생
        Users user = usersRepository.findUsersByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));

        // 사용자가 가입할 수 있는 최대 그룹 수를 초과했다면 예외처리
        int currentUserTeams = teamUserRepository.countByUser(user);
        if (currentUserTeams >= MAX_ALLOWED_TEAMS) {
            throw new GlobalBaseException(USER_NOT_FOUND);
        }

        // 그룹 검색 및 없을 경우 예외 발생
        Team team = teamRepository.findByTeamId(joinGroupRequest.getTeamId())
                .orElseThrow(() -> new GlobalBaseException(TEAM_NOT_FOUND));

        List<TeamUser> teamUsers = teamUserRepository.findByTeam(team);

        // 해당 그룹의 사용자 리스트를 불러와 정원 확인
        int currentTeamMembers = teamUserRepository.countByTeamId(team.getTeamId());
        if(currentTeamMembers>=MAX_TEAM_PEOPLE){
            throw new GlobalBaseException(TEAM_NOT_FOUND);
        }

        // 이미 그룹에 가입한 사용자인지 확인
        boolean isAlreadyMember = teamUserRepository.existsByTeamAndUser(team, user);
        if (isAlreadyMember) {
            throw new GlobalBaseException(TEAM_NOT_FOUND);
        }

        // 그룹에 사용자를 추가하고 저장
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .build();
        teamUserRepository.save(teamUser);

    }

    @Transactional
    public void createGroup(CreateGroupRequest createGroupRequest, int userId) {

        // 사용자를 검색하고 찾을 수 없다면 예외를 던집니다.
        Users user = usersRepository.findUsersByUserId(userId)
                .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));

        // 사용자의 팀 수가 허용된 최대 팀 수를 초과하는지 확인합니다.
        int currentUserTeams = teamUserRepository.countByUser(user);
        if (currentUserTeams >= MAX_ALLOWED_TEAMS) {
            throw new GlobalBaseException(USER_NOT_FOUND);
        }

        // 비밀번호 존재 여부에 따라 상태를 결정합니다.
        byte status = createGroupRequest.getPassword().isEmpty() ? (byte) 0 : (byte) 1;

        // 새 팀을 생성하고 저장합니다.
        Team team = Team.builder()
                .name(createGroupRequest.getTeamName())
                .explanation(createGroupRequest.getExplanation())
                .password(createGroupRequest.getPassword())
                .status(status)
                .user(user)
                .build();
        teamRepository.save(team);

        // 새 팀-사용자 연결을 생성하고 저장합니다.
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .build();
        teamUserRepository.save(teamUser);

    }

    public List<TeamUsersResponse> getGroupMembersInfo(int teamId) {

        // 팀을 검색하고 찾을 수 없다면 예외를 던집니다.
        Team team = teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new GlobalBaseException(TEAM_NOT_FOUND));

        List<TeamUser> teamUsers = teamUserRepository.findByTeam(team);

        List<TeamUsersResponse> teamUsersResponses = new ArrayList<>();

        for (TeamUser teamUser : teamUsers) {

            Users user = teamUser.getUser();

            UserDetail userDetail = userDetailRepository.findUserDetailByUser(user)
                    .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));


            UserStep userStep = userStepRepository.findUserStepByUser(user)
                    .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));

            TeamUsersResponse teamUsersResponse = TeamUsersResponse.from(user,userDetail,userStep);
            teamUsersResponses.add(teamUsersResponse);
        }

        return teamUsersResponses;
    }
}
