import { IQuizBowlUser, NewQuizBowlUser } from './quiz-bowl-user.model';

export const sampleWithRequiredData: IQuizBowlUser = {
  id: 17943,
};

export const sampleWithPartialData: IQuizBowlUser = {
  id: 12875,
  score: 26572,
};

export const sampleWithFullData: IQuizBowlUser = {
  id: 19126,
  score: 14228,
  organization: 'equally crewmate yummy',
};

export const sampleWithNewData: NewQuizBowlUser = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
