import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'quiz-bowl-user',
    data: { pageTitle: 'QuizBowlUsers' },
    loadChildren: () => import('./quiz-bowl-user/quiz-bowl-user.routes'),
  },
  {
    path: 'question',
    data: { pageTitle: 'Questions' },
    loadChildren: () => import('./question/question.routes'),
  },
  {
    path: 'answer',
    data: { pageTitle: 'Answers' },
    loadChildren: () => import('./answer/answer.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
