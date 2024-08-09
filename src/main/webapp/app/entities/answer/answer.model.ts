import { IQuestion } from 'app/entities/question/question.model';
import { IUser } from 'app/entities/user/user.model';

export interface IAnswer {
  id: number;
  answerText?: string | null;
  isCorrect?: boolean | null;
  visible?: boolean | null;
  question?: IQuestion | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewAnswer = Omit<IAnswer, 'id'> & { id: null };
