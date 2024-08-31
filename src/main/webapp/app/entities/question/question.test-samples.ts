import { IQuestion, NewQuestion } from './question.model';

export const sampleWithRequiredData: IQuestion = {
  id: 27585,
  number: 30587,
  questionText: 'grown easy',
  correctAnswer: 'abaft oh eek',
};

export const sampleWithPartialData: IQuestion = {
  id: 32418,
  number: 9494,
  questionText: 'concentration',
  correctAnswer: 'than oh oh',
  enable: true,
  timer: 6744,
};

export const sampleWithFullData: IQuestion = {
  id: 21603,
  number: 24969,
  questionText: 'bulge',
  difficultyLevel: 5006,
  correctAnswer: 'but crawdad amongst',
  enable: true,
  timer: 4350,
};

export const sampleWithNewData: NewQuestion = {
  number: 19560,
  questionText: 'negligible yowza',
  correctAnswer: 'tall opposite',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
