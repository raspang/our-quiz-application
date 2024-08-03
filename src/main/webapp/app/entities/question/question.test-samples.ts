import { IQuestion, NewQuestion } from './question.model';

export const sampleWithRequiredData: IQuestion = {
  id: 9905,
  number: 27585,
  questionText: 'starchy reckless oof',
  correctAnswer: 'to',
};

export const sampleWithPartialData: IQuestion = {
  id: 23658,
  number: 7908,
  difficultyLevel: 8715,
  questionText: 'elicit',
  correctAnswer: 'concentration',
};

export const sampleWithFullData: IQuestion = {
  id: 25295,
  number: 18888,
  difficultyLevel: 31361,
  questionText: 'terribly so prolong',
  correctAnswer: 'anenst anti rightfully',
  enable: true,
};

export const sampleWithNewData: NewQuestion = {
  number: 18837,
  questionText: 'amongst',
  correctAnswer: 'opposite fool',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
