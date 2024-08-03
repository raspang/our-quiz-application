import { IAnswer, NewAnswer } from './answer.model';

export const sampleWithRequiredData: IAnswer = {
  id: 24147,
  answerText: 'delightfully collar',
};

export const sampleWithPartialData: IAnswer = {
  id: 8292,
  answerText: 'medium',
};

export const sampleWithFullData: IAnswer = {
  id: 11643,
  answerText: 'detailed grill',
  isCorrect: true,
};

export const sampleWithNewData: NewAnswer = {
  answerText: 'atop',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
