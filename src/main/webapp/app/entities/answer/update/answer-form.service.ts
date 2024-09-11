import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IAnswer, NewAnswer } from '../answer.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAnswer for edit and NewAnswerFormGroupInput for create.
 */
type AnswerFormGroupInput = IAnswer | PartialWithRequiredKeyOf<NewAnswer>;

type AnswerFormDefaults = Pick<NewAnswer, 'id' | 'isCorrect' | 'visible'>;

type AnswerFormGroupContent = {
  id: FormControl<IAnswer['id'] | NewAnswer['id']>;
  answerText: FormControl<IAnswer['answerText']>;
  isCorrect: FormControl<IAnswer['isCorrect']>;
  visible: FormControl<IAnswer['visible']>;
  question: FormControl<IAnswer['question']>;
  user: FormControl<IAnswer['user']>;
};

export type AnswerFormGroup = FormGroup<AnswerFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AnswerFormService {
  createAnswerFormGroup(answer: AnswerFormGroupInput = { id: null }): AnswerFormGroup {
    const answerRawValue = {
      ...this.getFormDefaults(),
      ...answer,
    };
    return new FormGroup<AnswerFormGroupContent>({
      id: new FormControl(
        { value: answerRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      answerText: new FormControl(answerRawValue.answerText, {
        validators: [Validators.required],
      }),
      isCorrect: new FormControl(answerRawValue.isCorrect),
      visible: new FormControl(answerRawValue.visible),
      question: new FormControl(
        { value: answerRawValue.question, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      user: new FormControl(
        { value: answerRawValue.user, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
    });
  }

  getAnswer(form: AnswerFormGroup): IAnswer | NewAnswer {
    return form.getRawValue() as IAnswer | NewAnswer;
  }

  resetForm(form: AnswerFormGroup, answer: AnswerFormGroupInput): void {
    const answerRawValue = { ...this.getFormDefaults(), ...answer };
    form.reset(
      {
        ...answerRawValue,
        id: { value: answerRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): AnswerFormDefaults {
    return {
      id: null,
      isCorrect: false,
      visible: false,
    };
  }
}
