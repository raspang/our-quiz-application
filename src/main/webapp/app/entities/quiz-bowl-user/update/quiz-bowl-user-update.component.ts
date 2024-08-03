import { Component, inject, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { IQuizBowlUser } from '../quiz-bowl-user.model';
import { QuizBowlUserService } from '../service/quiz-bowl-user.service';
import { QuizBowlUserFormService, QuizBowlUserFormGroup } from './quiz-bowl-user-form.service';

@Component({
  standalone: true,
  selector: 'jhi-quiz-bowl-user-update',
  templateUrl: './quiz-bowl-user-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class QuizBowlUserUpdateComponent implements OnInit {
  isSaving = false;
  quizBowlUser: IQuizBowlUser | null = null;

  usersSharedCollection: IUser[] = [];

  protected quizBowlUserService = inject(QuizBowlUserService);
  protected quizBowlUserFormService = inject(QuizBowlUserFormService);
  protected userService = inject(UserService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: QuizBowlUserFormGroup = this.quizBowlUserFormService.createQuizBowlUserFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ quizBowlUser }) => {
      this.quizBowlUser = quizBowlUser;
      if (quizBowlUser) {
        this.updateForm(quizBowlUser);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const quizBowlUser = this.quizBowlUserFormService.getQuizBowlUser(this.editForm);
    if (quizBowlUser.id !== null) {
      this.subscribeToSaveResponse(this.quizBowlUserService.update(quizBowlUser));
    } else {
      this.subscribeToSaveResponse(this.quizBowlUserService.create(quizBowlUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IQuizBowlUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(quizBowlUser: IQuizBowlUser): void {
    this.quizBowlUser = quizBowlUser;
    this.quizBowlUserFormService.resetForm(this.editForm, quizBowlUser);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, quizBowlUser.user);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.quizBowlUser?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
