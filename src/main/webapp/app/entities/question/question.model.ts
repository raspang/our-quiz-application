export interface IQuestion {
  id: number;
  number?: number | null;
  questionText?: string | null;
  difficultyLevel?: number | null;
  correctAnswer?: string | null;
  enable?: boolean | null;
}

export type NewQuestion = Omit<IQuestion, 'id'> & { id: null };
