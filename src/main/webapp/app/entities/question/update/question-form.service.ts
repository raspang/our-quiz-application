import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IQuestion, NewQuestion } from '../question.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IQuestion for edit and NewQuestionFormGroupInput for create.
 */
type QuestionFormGroupInput = IQuestion | PartialWithRequiredKeyOf<NewQuestion>;

type QuestionFormDefaults = Pick<NewQuestion, 'id' | 'enable'>;

type QuestionFormGroupContent = {
  id: FormControl<IQuestion['id'] | NewQuestion['id']>;
  number: FormControl<IQuestion['number']>;
  questionText: FormControl<IQuestion['questionText']>;
  difficultyLevel: FormControl<IQuestion['difficultyLevel']>;
  correctAnswer: FormControl<IQuestion['correctAnswer']>;
  enable: FormControl<IQuestion['enable']>;
  correctAnswer2: FormControl<IQuestion['correctAnswer2']>;
  correctAnswer3: FormControl<IQuestion['correctAnswer3']>;
  correctAnswer4: FormControl<IQuestion['correctAnswer4']>;
};

export type QuestionFormGroup = FormGroup<QuestionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class QuestionFormService {
  createQuestionFormGroup(question: QuestionFormGroupInput = { id: null }): QuestionFormGroup {
    const questionRawValue = {
      ...this.getFormDefaults(),
      ...question,
    };
    return new FormGroup<QuestionFormGroupContent>({
      id: new FormControl(
        { value: questionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      number: new FormControl(questionRawValue.number, {
        validators: [Validators.required],
      }),
      questionText: new FormControl(questionRawValue.questionText, {
        validators: [Validators.required],
      }),
      difficultyLevel: new FormControl(questionRawValue.difficultyLevel),
      correctAnswer: new FormControl(questionRawValue.correctAnswer, {
        validators: [Validators.required],
      }),
      enable: new FormControl(
        { value: questionRawValue.enable, disabled: true },
        {
          validators: [Validators.required],
        },
      ),
      correctAnswer2: new FormControl(questionRawValue.correctAnswer2),
      correctAnswer3: new FormControl(questionRawValue.correctAnswer3),
      correctAnswer4: new FormControl(questionRawValue.correctAnswer4),
    });
  }

  getQuestion(form: QuestionFormGroup): IQuestion | NewQuestion {
    return form.getRawValue() as IQuestion | NewQuestion;
  }

  resetForm(form: QuestionFormGroup, question: QuestionFormGroupInput): void {
    const questionRawValue = { ...this.getFormDefaults(), ...question };
    form.reset(
      {
        ...questionRawValue,
        id: { value: questionRawValue.id, disabled: true },
        number: question.number ?? 0, // Initialize score to 0 if it's not provided
        difficultyLevel: question.difficultyLevel ?? 1, // Initialize score to 0 if it's not provided
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): QuestionFormDefaults {
    return {
      id: null,
      enable: false,
    };
  }
}
