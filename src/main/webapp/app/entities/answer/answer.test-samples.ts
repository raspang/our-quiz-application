import { IAnswer, NewAnswer } from './answer.model';

export const sampleWithRequiredData: IAnswer = {
  id: 12001,
  answerText: 'bland upwardly ravel',
};

export const sampleWithPartialData: IAnswer = {
  id: 24211,
  answerText: 'mmm enfold',
  isCorrect: false,
};

export const sampleWithFullData: IAnswer = {
  id: 22901,
  answerText: 'rightfully',
  isCorrect: false,
  visible: false,
};

export const sampleWithNewData: NewAnswer = {
  answerText: 'front',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
