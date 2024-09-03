import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IQuestion } from 'app/entities/question/question.model';
//import { QuestionService } from 'app/entities/question/service/question.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { AnswerService } from '../service/answer.service';
import { IAnswer } from '../answer.model';
import { AnswerFormService, AnswerFormGroup } from './answer-form.service';

@Component({
  standalone: true,
  selector: 'jhi-answer-update',
  templateUrl: './answer-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class AnswerUpdateComponent implements OnInit {
  isSaving = false;
  answer: IAnswer | null = null;

  questionsSharedCollection: IQuestion[] = [];
  usersSharedCollection: IUser[] = [];

  protected answerService = inject(AnswerService);
  protected answerFormService = inject(AnswerFormService);
  //protected questionService = inject(QuestionService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AnswerFormGroup = this.answerFormService.createAnswerFormGroup();

  // compareQuestion = (o1: IQuestion | null, o2: IQuestion | null): boolean => this.questionService.compareQuestion(o1, o2);

  //compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ answer }) => {
      this.answer = answer;
      if (answer) {
        this.updateForm(answer);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const answer = this.answerFormService.getAnswer(this.editForm);
    if (answer.id !== null) {
      // this.subscribeToSaveResponse(this.answerService.update(answer));
    } else {
      this.subscribeToSaveResponse(this.answerService.create(answer));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAnswer>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    // this.editForm.get('answerText')?.reset('');
    this.editForm.reset();
  }

  protected onSaveError(): void {
    // Api for inheritance.
    this.editForm.reset();
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(answer: IAnswer): void {
    // this.answer = answer;
    // this.answerFormService.resetForm(this.editForm, answer);
    // this.questionsSharedCollection = this.questionService.addQuestionToCollectionIfMissing<IQuestion>(
    //   this.questionsSharedCollection,
    //   answer.question,
    // );
    // this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, answer.user);
  }

  protected loadRelationshipsOptions(): void {
    // this.questionService
    //   .query()
    //   .pipe(map((res: HttpResponse<IQuestion[]>) => res.body ?? []))
    //   .pipe(
    //     map((questions: IQuestion[]) => this.questionService.addQuestionToCollectionIfMissing<IQuestion>(questions, this.answer?.question)),
    //   )
    //   .subscribe((questions: IQuestion[]) => (this.questionsSharedCollection = questions));
    // this.userService
    //   .query()
    //   .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
    //   .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.answer?.user)))
    //   .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
