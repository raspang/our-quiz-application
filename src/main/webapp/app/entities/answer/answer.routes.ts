import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AnswerComponent } from './list/answer.component';
import { AnswerDetailComponent } from './detail/answer-detail.component';
import { AnswerUpdateComponent } from './update/answer-update.component';
import AnswerResolve from './route/answer-routing-resolve.service';

const answerRoute: Routes = [
  {
    path: '',
    component: AnswerComponent,
    data: {
      defaultSort: 'id,' + ASC,
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AnswerDetailComponent,
    resolve: {
      answer: AnswerResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AnswerUpdateComponent,
    resolve: {
      answer: AnswerResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AnswerUpdateComponent,
    resolve: {
      answer: AnswerResolve,
    },
    data: {
      authorities: ['ROLE_ADMIN'],
    },
    canActivate: [UserRouteAccessService],
  },
];

export default answerRoute;
