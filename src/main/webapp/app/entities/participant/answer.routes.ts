import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { AnswerDetailComponent } from './detail/answer-detail.component';
import { AnswerUpdateComponent } from './update/answer-update.component';
import AnswerResolve from './route/answer-routing-resolve.service';
import { AnswerComponent } from './list/answer.component';

const answerRoute: Routes = [
  {
    path: '',
    // component: AnswerComponent,
    // data: {
    //   defaultSort: 'id,' + ASC,
    //   authorities: ['ROLE_PARTICIPANT'],
    // },
    // canActivate: [UserRouteAccessService],
    component: AnswerUpdateComponent,
    resolve: {
      answer: AnswerResolve,
    },
    data: {
      authorities: ['ROLE_PARTICIPANT'],
    },
    canActivate: [UserRouteAccessService],
  },

  {
    path: 'my-list-result',
    component: AnswerComponent,
    data: {
      defaultSort: 'id,' + ASC,
      authorities: ['ROLE_PARTICIPANT'],
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
      authorities: ['ROLE_PARTICIPANT'],
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
      authorities: ['ROLE_PARTICIPANT'],
    },
    canActivate: [UserRouteAccessService],
  },
  // {
  //   path: ':id/edit',
  //   component: AnswerUpdateComponent,
  //   resolve: {
  //     answer: AnswerResolve,
  //   },
  //   data: {
  //     authorities: ['ROLE_PARTICIPANT'],
  //   },
  //   canActivate: [UserRouteAccessService],
  // },
];

export default answerRoute;
