package gachonproject.web.dto;


import gachonproject.web.domain.AnalysisActivity;
import gachonproject.web.domain.UserActivity;
import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {

    private int Number_of_real_time_users;
    private int Number_of_users_today;
    private int Number_of_use_analysis_service;
    private int Number_of_unanswered_questions;
    private int Number_of_Cosmetics_can_registered;

    private List<UserActivity> user_activities_status;
    private List<AnalysisActivity> analysis_activities_status;

    private ModelRateDTO recommendation_evaluation_status;
    private ModelRateDTO analysis_service_evaluation_status;

    public DashboardDTO(List<AnalysisActivity> analysis_activities_status, ModelRateDTO analysis_service_evaluation_status,
                        int number_of_Cosmetics_can_registered, int number_of_real_time_users, int number_of_unanswered_questions,
                        int number_of_use_analysis_service, int number_of_users_today,
                        ModelRateDTO recommendation_evaluation_status, List<UserActivity> user_activities_status) {

        this.analysis_activities_status = analysis_activities_status;
        this.analysis_service_evaluation_status = analysis_service_evaluation_status;
        this.Number_of_Cosmetics_can_registered = number_of_Cosmetics_can_registered;
        this.Number_of_real_time_users = number_of_real_time_users;
        this.Number_of_unanswered_questions = number_of_unanswered_questions;
        this.Number_of_use_analysis_service = number_of_use_analysis_service;
        this.Number_of_users_today = number_of_users_today;
        this.recommendation_evaluation_status = recommendation_evaluation_status;
        this.user_activities_status = user_activities_status;
    }
}
