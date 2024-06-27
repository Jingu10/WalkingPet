package com.walkerholic.walkingpet.domain.team.service;

import com.walkerholic.walkingpet.domain.item.repository.UserItemRepository;
import com.walkerholic.walkingpet.domain.team.dto.request.CreateGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.EnterGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.ExitGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.request.JoinGroupRequest;
import com.walkerholic.walkingpet.domain.team.dto.response.TeamDetailResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.walkerholic.walkingpet.global.error.GlobalErrorCode.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamService {

    private static final int MAX_ALLOWED_TEAMS  = 3;
    private static final int MAX_TEAM_PEOPLE  = 6;
    private static final byte STATUS_NO_PASSWORD = 0;
    private static final byte STATUS_WITH_PASSWORD = 1;
    private final int GROUP_GOAL_STEP = 10000;
    private final int GROUP_GOAL_REWARD_POINT = 50;
    private final int GROUP_GOAL_REWARD_BOX= 2;


    private final TeamRepository teamRepository;
    private final TeamUserRepository teamUserRepository;
    private final UsersRepository usersRepository;
    private final UserDetailRepository userDetailRepository;
    private final UserStepRepository userStepRepository;
    private final UserItemRepository userItemRepository;

    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeam(int userId) {

        // 사용자가 가입한 팀들을 제외한 모든 팀 조회
        List<Team> allTeams = teamRepository.findNotJoinedTeams(userId);
        return getTeamResponses(allTeams);
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> getUserTeams(int userId) {

        List<Team> teams = teamUserRepository.findTeamsByUserId(userId);

        if (teams.isEmpty()) {
            // 사용자가 가입한 그룹이 없으면 빈 배열 반환
            return Collections.emptyList();
        } else {
            return getTeamResponses(teams);
        }

    }
    @Transactional(readOnly = true)
    public List<TeamResponse> getSearchTeams(String content) {

        List<Team> teams = teamRepository.findByNameContaining(content);

        if (teams.isEmpty()) {
            // 검색 결과에 맞는 그룹이 없으면 빈 배열 반환
            return Collections.emptyList();
        } else {
            return getTeamResponses(teams);
        }
    }

    // Team 객체에 userCount를 추가해서 TeamResponse 형식에 맞추는 메소드
    private List<TeamResponse> getTeamResponses(List<Team> teams) {

        return teams.stream()
                .map(team -> {
                    Integer userCount = getUserCountByTeamId(team.getTeamId());
                    return TeamResponse.from(team, userCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void enterGroup(EnterGroupRequest enterGroupRequest, Integer userId) {
        Team team = getTeamById(enterGroupRequest.getTeamId());

        if (team == null) {
            // 팀이 존재하지 않는 경우
            throw new GlobalBaseException(TEAM_NOT_FOUND);
        }

        if(!enterGroupRequest.getPassword().equals(team.getPassword())){
            // 받아온 비밀번호와 팀의 비밀번호가 일치하지 않는 경우
            throw new GlobalBaseException(TEAM_PASSWORD_INCORRECT);
        }
    }

    @Transactional(readOnly = true)
    public TeamDetailResponse getTeamDetail(int teamId, int userId) {

        Team team = getTeamById(teamId);

//        Integer userCount = getUserCountByTeamId(team.getTeamId());
        List<TeamUsersResponse> teamUsersResponses = getGroupMembersInfo(teamId);

        TeamResponse teamResponse = TeamResponse.from(team, teamUsersResponses.size()); // -> count query가 아닌 list 사이즈로 변경

        boolean isJoin = teamUsersResponses.stream().anyMatch(teamUser -> teamUser.getUserId().equals(userId));

        int teamTotalSteps = teamUsersResponses.stream()
                .mapToInt(TeamUsersResponse::getStep)
                .sum();


        return TeamDetailResponse.from(teamResponse, teamUsersResponses,teamTotalSteps,isJoin);
    }

    @Transactional(readOnly = false)
    public void joinGroup(JoinGroupRequest joinGroupRequest,int userId) {

        Users user = getUserById(userId);

        // 사용자가 가입할 수 있는 최대 그룹 수를 초과했다면 예외처리
        int currentUserTeams = getCurrentUserTeamCount(user);
        if (currentUserTeams >= MAX_ALLOWED_TEAMS) {
            throw new GlobalBaseException(JOIN_TEAMS_EXCEEDED);
        }

        Team team = getTeamById(joinGroupRequest.getTeamId());

        // 해당 그룹의 사용자 리스트를 불러와 정원 확인
        int currentTeamMembers = getUserCountByTeamId(team.getTeamId());
        if(currentTeamMembers>=MAX_TEAM_PEOPLE){
            throw new GlobalBaseException(TEAM_MEMBER_EXCEEDED);
        }

        // 이미 그룹에 가입한 사용자인지 확인
        boolean isAlreadyMember = teamUserRepository.existsByTeamAndUser(team, user);
        if (isAlreadyMember) {
            throw new GlobalBaseException(ALREADY_JOINED_GROUP);
        }

        // 그룹에 사용자를 추가하고 저장
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .build();
        teamUserRepository.save(teamUser);
    }

    @Transactional(readOnly = false)
    public void createGroup(CreateGroupRequest createGroupRequest, int userId) {

        Users user = getUserById(userId);

        // 사용자의 그룹 수가 허용된 최대 그룹 수를 초과하는지 확인합니다.
        int currentUserTeams = getCurrentUserTeamCount(user);
        if (currentUserTeams >= MAX_ALLOWED_TEAMS) {
            throw new GlobalBaseException(JOIN_TEAMS_EXCEEDED);
        }

        // 비밀번호 존재 여부에 따라 상태를 결정합니다.
        byte status = createGroupRequest.getPassword().isEmpty() ? STATUS_NO_PASSWORD : STATUS_WITH_PASSWORD;

        // 새 그룹을 생성하고 저장합니다.
        Team team = Team.builder()
                .name(createGroupRequest.getTeamName())
                .explanation(createGroupRequest.getExplanation())
                .password(createGroupRequest.getPassword())
                .status(status)
                .user(user)
                .build();
        teamRepository.save(team);

        // 새 그룹-사용자 연결을 생성하고 저장합니다.
        TeamUser teamUser = TeamUser.builder()
                .team(team)
                .user(user)
                .build();
        teamUserRepository.save(teamUser);

    }

    @Transactional(readOnly = true)
    public List<TeamUsersResponse> getGroupMembersInfo(int teamId) {

        List<TeamUser> teamUsers = getTeamUsersByTeamId(teamId);

        List<TeamUsersResponse> teamUsersResponses = new ArrayList<>();
        for (TeamUser teamUser : teamUsers) {
            TeamUsersResponse teamUsersResponse = createTeamUsersResponse(teamUser);
            teamUsersResponses.add(teamUsersResponse);
        }

        return teamUsersResponses;
    }

    @Transactional(readOnly = false)
    public void exitGroup(ExitGroupRequest exitGroupRequest, int userId) {

        Users user = getUserById(userId);

        Team team = getTeamById(exitGroupRequest.getTeamId());

        TeamUser teamUser = teamUserRepository.findByUserAndTeam(user,team)
                .orElseThrow(() -> new GlobalBaseException(TEAM_USER_NOT_FOUND));

        // 그룹의 생성자가 현재 사용자인 경우
        if(team.getUser().getUserId()==userId){
            // 해당 그룹에 속한 모든 TeamUser 엔티티를 삭제하고 그룹 정보 자체도 삭제
            teamUserRepository.deleteByTeam(team);
            teamRepository.delete(team);
        }else {
            // 그룹의 생성자가 아니면 해당 사용자만 그룹에서 제거
            teamUserRepository.delete(teamUser);
        }
    }


    public boolean checkAvailableNickname(String teamName) {
        return teamRepository.existsByName(teamName);
    }

    // 주어진 사용자 ID에 해당하는 사용자를 데이터베이스에서 검색합니다.
    public Users getUserById(int userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new GlobalBaseException(USER_NOT_FOUND));
    }

    // 주어진 그룹 ID에 해당하는 그룹의 사용자 수를 반환합니다.
    public int getUserCountByTeamId(int teamId) {
        return teamUserRepository.countByTeamId(teamId);
    }

    // 주어진 teamId에 해당하는 그룹을 데이터베이스에서 검색합니다.
    public Team getTeamById(int teamId) {
        return teamRepository.findByTeamId(teamId)
                .orElseThrow(() -> new GlobalBaseException(TEAM_NOT_FOUND));
    }

    // 주어진 그룹에 속한 사용자 목록을 데이터베이스에서 검색합니다.
    public List<TeamUser> getTeamUsersByTeamId(int teamId) {
        return teamUserRepository.findByTeamId(teamId);
    }

    // 현재 사용자가 속한 그룹의 수를 반환합니다.
    public int getCurrentUserTeamCount(Users user) {
        return teamUserRepository.countByUser(user);
    }

    // 주어진 TeamUser 객체에서 사용자의 세부 정보와 단계 정보를 검색하여 TeamUsersResponse 객체를 생성합니다
    private TeamUsersResponse createTeamUsersResponse(TeamUser teamUser) {

        UserDetail userDetail = userDetailRepository.findByJoinFetchByUserId(teamUser.getUser().getUserId())
                .orElseThrow(() -> new GlobalBaseException(USER_DETAIL_NOT_FOUND));

        UserStep userStep = userStepRepository.findUserStepByUserUserId(teamUser.getUser().getUserId())
                .orElseThrow(() -> new GlobalBaseException(USER_STEP_NOT_FOUND));

        return TeamUsersResponse.from(userDetail,userStep);
    }

    // 자정에 모든 그룹의 목표 달성 여부 확인을 위한 메서드
    public void checkAllGroupGoal() {
        List<Team> teams = teamRepository.findAll();

        // 모든 그룹 탐색
        for (Team team: teams) {
            checkGoalGroupStep(team);
        }
    }
    
    // 그룹의 걸음수 확인하는 메서드
    @Transactional(readOnly = false)
    public void checkGoalGroupStep(Team team) {
        List<TeamUser> teamUsers = teamUserRepository.findByTeamId(team.getTeamId());

        // 그룹 내 유저들의 어제 걸음수 탐색
        int totalStep = teamUsers.stream()
                .mapToInt(teamUser -> userStepRepository.findUserYesterdayStep(teamUser.getUser().getUserId()))
                .sum();

        if (totalStep < GROUP_GOAL_STEP) return;

        // 해당 그룹이 그룹 목표 걸음수를 달성했을 경우 -> 그룹 포인트 50, 각 무지개 상자 + 1
        team.addPoint(GROUP_GOAL_REWARD_POINT);
        teamRepository.save(team);

        List<Integer> userIds = teamUsers.stream().map(teamUser -> teamUser.getUser().getUserId()).collect(Collectors.toList());
        userItemRepository.addUserLuxuryBoxQuantity(userIds, GROUP_GOAL_REWARD_BOX);
    }
}
