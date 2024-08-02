import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 31553,
  login: 'zG2enj',
};

export const sampleWithPartialData: IUser = {
  id: 29818,
  login: 'm',
};

export const sampleWithFullData: IUser = {
  id: 14846,
  login: 'lG',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
