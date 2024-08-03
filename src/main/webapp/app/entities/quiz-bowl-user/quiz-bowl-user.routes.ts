import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { QuizBowlUserComponent } from './list/quiz-bowl-user.component';
import { QuizBowlUserDetailComponent } from './detail/quiz-bowl-user-detail.component';
import { QuizBowlUserUpdateComponent } from './update/quiz-bowl-user-update.component';
import QuizBowlUserResolve from './route/quiz-bowl-user-routing-resolve.service';

const quizBowlUserRoute: Routes = [
  {
    path: '',
    component: QuizBowlUserComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuizBowlUserDetailComponent,
    resolve: {
      quizBowlUser: QuizBowlUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuizBowlUserUpdateComponent,
    resolve: {
      quizBowlUser: QuizBowlUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuizBowlUserUpdateComponent,
    resolve: {
      quizBowlUser: QuizBowlUserResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default quizBowlUserRoute;
