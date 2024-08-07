import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DESC } from 'app/config/navigation.constants';
import { QuizBowlUserComponent } from './list/quiz-bowl-user.component';
import { QuizBowlUserDetailComponent } from './detail/quiz-bowl-user-detail.component';
import { QuizBowlUserUpdateComponent } from './update/quiz-bowl-user-update.component';
import QuizBowlUserResolve from './route/quiz-bowl-user-routing-resolve.service';

const quizBowlUserRoute: Routes = [
  {
    path: '',
    component: QuizBowlUserComponent,
    data: {
      defaultSort: 'score,' + DESC,
      authorities: ['ROLE_ADMIN', 'ROLE_USER'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: QuizBowlUserDetailComponent,
    resolve: {
      quizBowlUser: QuizBowlUserResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: QuizBowlUserUpdateComponent,
    resolve: {
      quizBowlUser: QuizBowlUserResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: QuizBowlUserUpdateComponent,
    resolve: {
      quizBowlUser: QuizBowlUserResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default quizBowlUserRoute;
