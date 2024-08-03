import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { IQuizBowlUser } from '../quiz-bowl-user.model';

@Component({
  standalone: true,
  selector: 'jhi-quiz-bowl-user-detail',
  templateUrl: './quiz-bowl-user-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class QuizBowlUserDetailComponent {
  quizBowlUser = input<IQuizBowlUser | null>(null);

  previousState(): void {
    window.history.back();
  }
}
