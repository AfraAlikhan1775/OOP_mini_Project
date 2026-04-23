package com.util;

import java.util.Arrays;

public class MarksCalculator {

    public static double calculateCA(double q1, double q2, double q3,
                                     double assignment, double midExam) {

        validateRawMark(q1, "Quiz 1");
        validateRawMark(q2, "Quiz 2");
        validateRawMark(q3, "Quiz 3");
        validateRawMark(assignment, "Assignment");
        validateRawMark(midExam, "Mid Exam");

        double[] quizzes = {q1, q2, q3};
        Arrays.sort(quizzes);

        double bestQuiz = quizzes[2];
        double secondBestQuiz = quizzes[1];

        double quizPart = (bestQuiz / 100.0) * 5 + (secondBestQuiz / 100.0) * 5;
        double assignmentPart = (assignment / 100.0) * 10;
        double midPart = (midExam / 100.0) * 20;

        return round2(quizPart + assignmentPart + midPart);
    }

    public static double calculateFinalMark(double ca,
                                            double finalTheory,
                                            double finalPractical,
                                            boolean hasTheory,
                                            boolean hasPractical) {

        validateWeightedMark(ca, "CA");
        validateRawMark(finalTheory, "Final Theory");
        validateRawMark(finalPractical, "Final Practical");

        double total = ca;

        if (hasTheory && hasPractical) {
            total += (finalTheory / 100.0) * 30;
            total += (finalPractical / 100.0) * 30;
        } else if (hasTheory) {
            total += (finalTheory / 100.0) * 60;
        } else if (hasPractical) {
            total += (finalPractical / 100.0) * 60;
        } else {
            throw new IllegalArgumentException("Course must have theory, practical, or both.");
        }

        return round2(total);
    }

    private static void validateRawMark(double mark, String label) {
        if (mark < 0 || mark > 100) {
            throw new IllegalArgumentException(label + " must be between 0 and 100.");
        }
    }

    private static void validateWeightedMark(double mark, String label) {
        if (mark < 0 || mark > 40) {
            throw new IllegalArgumentException(label + " must be between 0 and 40.");
        }
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}