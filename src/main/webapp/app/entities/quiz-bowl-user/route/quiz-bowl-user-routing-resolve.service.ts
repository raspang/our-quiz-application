import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IQuizBowlUser } from '../quiz-bowl-user.model';
import { QuizBowlUserService } from '../service/quiz-bowl-user.service';

const quizBowlUserResolve = (route: ActivatedRouteSnapshot): Observable<null | IQuizBowlUser> => {
  const id = route.params['id'];
  if (id) {
    return inject(QuizBowlUserService)
      .find(id)
      .pipe(
        mergeMap((quizBowlUser: HttpResponse<IQuizBowlUser>) => {
          if (quizBowlUser.body) {
            return of(quizBowlUser.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default quizBowlUserResolve;
