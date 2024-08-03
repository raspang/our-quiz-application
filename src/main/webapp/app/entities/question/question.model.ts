export interface IQuestion {
  id: number;
  number?: number | null;
  difficultyLevel?: number | null;
  questionText?: string | null;
  correctAnswer?: string | null;
  enable?: boolean | null;
}

export type NewQuestion = Omit<IQuestion, 'id'> & { id: null };
