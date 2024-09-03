import { IQuizBowlUser, NewQuizBowlUser } from './quiz-bowl-user.model';

export const sampleWithRequiredData: IQuizBowlUser = {
  id: 17943,
};

export const sampleWithPartialData: IQuizBowlUser = {
  id: 12875,
  organization: 'titter psst supernatural',
};

export const sampleWithFullData: IQuizBowlUser = {
  id: 19126,
  organization: 'nor unless naturally',
  score: 341,
};

export const sampleWithNewData: NewQuizBowlUser = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
