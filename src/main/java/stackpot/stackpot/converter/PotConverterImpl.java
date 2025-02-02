package stackpot.stackpot.converter;

import org.springframework.stereotype.Component;
import stackpot.stackpot.domain.Pot;
import stackpot.stackpot.domain.PotRecruitmentDetails;
import stackpot.stackpot.domain.User;
import stackpot.stackpot.domain.enums.PotModeOfOperation;
import stackpot.stackpot.domain.enums.Role;
import stackpot.stackpot.web.dto.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.time.format.DateTimeFormatter;

@Component
public class PotConverterImpl implements PotConverter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    public Pot toEntity(PotRequestDto requestDto, User user) {
        return Pot.builder()
                .potName(requestDto.getPotName())
                .potStartDate(requestDto.getPotStartDate())
                .potEndDate(requestDto.getPotEndDate())
                .potDuration(requestDto.getPotDuration())
                .potLan(requestDto.getPotLan())
                .potContent(requestDto.getPotContent())
                .potStatus(requestDto.getPotStatus())
                .potModeOfOperation(PotModeOfOperation.valueOf(requestDto.getPotModeOfOperation()))
                .potSummary(requestDto.getPotSummary())
                .recruitmentDeadline(requestDto.getRecruitmentDeadline())
                .user(user) // 사용자 설정
                .build();
    }

    @Override
    public PotResponseDto toDto(Pot entity, List<PotRecruitmentDetails> recruitmentDetails) {
        return PotResponseDto.builder()
                .potId(entity.getPotId())
                .potName(entity.getPotName())
                .potStartDate(formatDate(entity.getPotStartDate()))
                .potEndDate(formatDate(entity.getPotEndDate()))
                .potDuration(entity.getPotDuration())
                .potLan(entity.getPotLan())
                .potContent(entity.getPotContent())
                .potStatus(entity.getPotStatus())
                .potModeOfOperation(entity.getPotModeOfOperation().name())
                .potSummary(entity.getPotSummary())
                .recruitmentDeadline(entity.getRecruitmentDeadline())
                .recruitmentDetails(recruitmentDetails.stream().map(r -> PotRecruitmentResponseDto.builder()
                        .recruitmentRole(r.getRecruitmentRole().name())
                        .recruitmentCount(r.getRecruitmentCount())
                        .build()).collect(Collectors.toList()))
                .build();
    }

    public PotPreviewResponseDto toPrviewDto(User user, Pot pot, String recruitmentRoles) {
        LocalDate today = LocalDate.now();
        LocalDate deadline = pot.getRecruitmentDeadline();

        long daysDiff = ChronoUnit.DAYS.between(today, deadline);

        String dDay;
        if (daysDiff == 0) {
            dDay = "D-Day";
        } else if (daysDiff > 0) {
            dDay = "D-" + daysDiff;
        } else {
            dDay = "D+" + Math.abs(daysDiff);
        }

        return PotPreviewResponseDto.builder()
                .userId(user.getId())
                .userRole(String.valueOf(user.getRole()))
                .userNickname(user.getNickname() + getVegetableNameByRole(user.getRole().name()))
                .potId(pot.getPotId())
                .potName(pot.getPotName())
                .potContent(pot.getPotContent())
                .recruitmentRole(recruitmentRoles)  // 콤마로 연결된 역할 문자열
                .dDay(dDay)
                .build();
    }


    private String formatDate(java.time.LocalDate date) {
        return (date != null) ? date.format(DATE_FORMATTER) : "N/A";
    }

    @Override

    public CompletedPotResponseDto toCompletedPotResponseDto(Pot pot, Map<String, Integer> roleCounts, Role userPotRole, List<BadgeDto> myBadges) {
        return CompletedPotResponseDto.builder()
                .potId(pot.getPotId())
                .potName(pot.getPotName())
                .potStartDate(pot.getPotStartDate())
                .potEndDate(pot.getPotEndDate())
                .potLan(pot.getPotLan())
                .recruitmentDetails(pot.getRecruitmentDetails().stream()
                        .map(rd -> RecruitmentDetailResponseDto.builder()
                                .recruitmentRole(rd.getRecruitmentRole().name()) // Enum을 String으로 변환
                                .recruitmentCount(rd.getRecruitmentCount())
                                .build())
                        .collect(Collectors.toList()))
                .roleCounts(roleCounts)
                .userPotRole(userPotRole)
                .myBadges(myBadges)
                .build();
    }


    public PotSearchResponseDto toSearchDto(Pot pot) {
        // 역할 이름 매핑 (유효한 역할만 처리)
        String roleName = pot.getUser() != null && pot.getUser().getRole() != null
                ? pot.getUser().getRole().name()
                : "멤버";

        String nicknameWithRole = pot.getUser() != null && pot.getUser().getNickname() != null
                ? pot.getUser().getNickname() + " " + mapRoleName(roleName)
                : "Unknown 멤버";

        return PotSearchResponseDto.builder()
                .potId(pot.getPotId())
                .potName(pot.getPotName())
                .potContent(pot.getPotContent())
                .creatorNickname(nicknameWithRole)
                .creatorRole(
                        pot.getUser() != null && pot.getUser().getRole() != null
                                ? pot.getUser().getRole().name()
                                : "멤버" // 기본값 설정
                )
                .recruitmentPart(
                        pot.getRecruitmentDetails() != null
                                ? pot.getRecruitmentDetails().stream()
                                .filter(rd -> rd.getRecruitmentRole() != null)
                                .map(rd -> rd.getRecruitmentRole().name())
                                .collect(Collectors.joining(", "))
                                : "없음" // 기본값 설정
                )
                .recruitmentDeadline(pot.getRecruitmentDeadline())
                .build();
    }


    private String mapRoleName(String roleName) {
        return switch (roleName) {
            case "BACKEND" -> "양파";
            case "FRONTEND" -> "버섯";
            case "DESIGN" -> "브로콜리";
            case "PLANNING" -> "당근";
            default -> "멤버";
        };
    }

    private String getVegetableNameByRole(String role) {
        Map<String, String> roleToVegetableMap = Map.of(
                "BACKEND", " 양파",
                "FRONTEND", " 버섯",
                "DESIGN", " 브로콜리",
                "PLANNING", " 당근"
        );
        return roleToVegetableMap.getOrDefault(role, "알 수 없음");
    }

    private String getKoreanModeOfOperation(String modeOfOperation) {
        Map<String, String> modeOfOperationToKoreanMap = Map.of(
                "ONLINE", " 온라인",
                "OFFLINE", " 오프라인",
                "HYBRID", " 혼합"
        );
        return modeOfOperationToKoreanMap.getOrDefault(modeOfOperation, "알 수 없음");
    }
}