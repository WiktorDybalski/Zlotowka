
package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.GeneralPlanDTO;
import com.agh.zlotowka.model.Plan;
import com.agh.zlotowka.model.PlanType;
import com.agh.zlotowka.model.Subplan;
import com.agh.zlotowka.repository.PlanRepository;
import com.agh.zlotowka.repository.SubPlanRepository;
import com.agh.zlotowka.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralPlansService {
    private final PlanRepository planRepository;
    private final SubPlanRepository subPlanRepository;
    private final UserRepository userRepository;
    private final CurrencyService currencyService;

    public List<GeneralPlanDTO> getAllUncompletedPlans(Integer userId) {
        String userCurrencyCode = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with ID %d not found", userId)))
                .getCurrency()
                .getIsoCode();

        List<Plan> plans = planRepository.findAllUncompletedByUser(userId);
        List<GeneralPlanDTO> result = new ArrayList<>();

        for (Plan plan : plans) {
            String planCurrencyCode = plan.getCurrency().getIsoCode();

            GeneralPlanDTO generalPlanDTO = createPlanDTO(plan, planCurrencyCode, userCurrencyCode);

            List<Subplan> subplans = subPlanRepository.findAllUncompletedSubPlansByPlanId(plan.getPlanId());
            for (Subplan subplan : subplans) {
                generalPlanDTO.subplans().add(createSubplanDTO(subplan, planCurrencyCode, userCurrencyCode));
            }
            result.add(generalPlanDTO);
        }

        return result.stream()
                .sorted(Comparator.comparing(GeneralPlanDTO::requiredAmount))
                .toList();
    }

    private GeneralPlanDTO createPlanDTO(Plan plan, String planCurrencyCode, String userCurrencyCode) {
        BigDecimal convertedAmount = convertAmount(plan.getRequiredAmount(), planCurrencyCode, userCurrencyCode);

        return new GeneralPlanDTO(
                convertedAmount,
                plan.getName(),
                PlanType.PLAN,
                new ArrayList<>()
        );
    }

    private GeneralPlanDTO createSubplanDTO(Subplan subplan, String planCurrencyCode, String userCurrencyCode) {
        BigDecimal convertedAmount = convertAmount(subplan.getRequiredAmount(), planCurrencyCode, userCurrencyCode);

        return new GeneralPlanDTO(
                convertedAmount,
                subplan.getName(),
                PlanType.SUBPLAN,
                null
        );
    }

    private BigDecimal convertAmount(BigDecimal amount, String sourceCurrencyCode, String targetCurrencyCode) {
        if (sourceCurrencyCode.equals(targetCurrencyCode)) {
            return amount;
        }
        BigDecimal convertedAmount = BigDecimal.ZERO;
        try {
            convertedAmount = currencyService.convertCurrency(amount, sourceCurrencyCode, targetCurrencyCode);
        } catch (Exception e) {
            log.error("Unexpected error from CurrencyService", e);
        }
        return convertedAmount;
    }
}