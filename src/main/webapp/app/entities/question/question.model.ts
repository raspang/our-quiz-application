export interface IQuestion {
  id: number;
  number?: number | null;
  questionText?: string | null;
  difficultyLevel?: number | null;
  correctAnswer?: string | null;
  enable?: boolean | null;
  correctAnswer2?: string | null;
  correctAnswer3?: string | null;
  correctAnswer4?: string | null;
}

export type NewQuestion = Omit<IQuestion, 'id'> & { id: null };
