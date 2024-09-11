import { IQuestion, NewQuestion } from './question.model';

export const sampleWithRequiredData: IQuestion = {
  id: 18898,
  number: 26815,
  questionText: 'depressive',
  correctAnswer: 'nearly nor until',
};

export const sampleWithPartialData: IQuestion = {
  id: 22745,
  number: 3300,
  questionText: 'huzzah',
  difficultyLevel: 13349,
  correctAnswer: 'burly bank daffodil',
  enable: false,
  correctAnswer2: 'for',
  correctAnswer3: 'unless',
};

export const sampleWithFullData: IQuestion = {
  id: 30816,
  number: 22903,
  questionText: 'rightfully',
  difficultyLevel: 15765,
  correctAnswer: 'market yippee',
  enable: false,
  correctAnswer2: 'fool beside',
  correctAnswer3: 'upside-down after',
  correctAnswer4: 'if',
};

export const sampleWithNewData: NewQuestion = {
  number: 6873,
  questionText: 'apud ugh redistribute',
  correctAnswer: 'gamebird quicker',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
