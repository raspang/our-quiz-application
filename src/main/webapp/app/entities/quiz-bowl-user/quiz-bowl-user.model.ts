import { IUser } from 'app/entities/user/user.model';

export interface IQuizBowlUser {
  id: number;
  score?: number | null;
  organization?: string | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewQuizBowlUser = Omit<IQuizBowlUser, 'id'> & { id: null };
