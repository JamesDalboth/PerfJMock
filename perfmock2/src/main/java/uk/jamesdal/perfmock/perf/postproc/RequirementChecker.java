package uk.jamesdal.perfmock.perf.postproc;

import uk.jamesdal.perfmock.perf.exceptions.RequirementFailureException;
import uk.jamesdal.perfmock.perf.annotations.PerfRequirement;

public class RequirementChecker {
    private static void doesStatsMatchRequirement(PerfStatistics stats, PerfRequirement requirement) throws RequirementFailureException {
        double value1;
        switch (requirement.mode()) {
            case MEAN:
                value1 = stats.meanMeasuredTime();
                break;
            case MAX:
                value1 = stats.maxMeasuredTime();
                break;
            case MIN:
                value1 = stats.minMeasuredTime();
                break;
            default:
                value1 = 0.0;
                break;
        }

        double value2 = requirement.value();

        boolean success;
        switch (requirement.comparator()) {
            case LESS_THAN:
                success = value1 < value2;
                break;
            case EQUAL_TO:
                success = value1 == value2;
                break;
            case GREATER_THAN:
                success = value1 > value2;
                break;
            default:
                success = false;
                break;
        }

        if (!success) {
            System.err.println("Failure to meet requirement " + requirement + " actual value " + value1);
            throw new RequirementFailureException();
        }
    }

    public static void doesStatsMatchRequirements(PerfStatistics stats, PerfRequirement[] requirements) throws RequirementFailureException {
        for (PerfRequirement requirement : requirements) {
            doesStatsMatchRequirement(stats, requirement);
        }
    }
}
