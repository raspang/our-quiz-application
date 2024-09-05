package gov.pglds.ourquizapp.service;

import gov.pglds.ourquizapp.domain.Answer;
import gov.pglds.ourquizapp.domain.Question;
import gov.pglds.ourquizapp.repository.ParticipantRepository;
import gov.pglds.ourquizapp.service.dto.AnswerDTO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public List<AnswerDTO> getQuestionsWithAnswerStatus() {
        // Get all questions that the user has answered
        List<Answer> takenAnswers = participantRepository.findAllTaken();
        Set<Long> answeredQuestionIds = takenAnswers.stream().map(answer -> answer.getQuestion().getId()).collect(Collectors.toSet());

        // Get all questions that the user has not answered
        List<Question> unansweredQuestions = participantRepository.findUnansweredQuestionsWithExistingAnswers();

        // Combine answered and unanswered questions into a single list
        List<AnswerDTO> result = new ArrayList<>();

        // Add all answered questions with the flag set to true
        for (Answer answer : takenAnswers) {
            result.add(new AnswerDTO(answer));
        }

        // Add all unanswered questions with the flag set to false
        for (Question question : unansweredQuestions) {
            if (!answeredQuestionIds.contains(question.getId())) {
                AnswerDTO answerDTO = new AnswerDTO();
                answerDTO.setQuestion(question);
                answerDTO.setVisible(false);
                result.add(answerDTO);
            }
        }

        // Sort the result by question number
        result.sort(Comparator.comparing(dto -> dto.getQuestion().getNumber()));

        return result;
    }
}
