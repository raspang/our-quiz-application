import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IQuizBowlUser } from '../quiz-bowl-user.model';
import { QuizBowlUserService } from '../service/quiz-bowl-user.service';

@Component({
  standalone: true,
  templateUrl: './quiz-bowl-user-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class QuizBowlUserDeleteDialogComponent {
  quizBowlUser?: IQuizBowlUser;

  protected quizBowlUserService = inject(QuizBowlUserService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.quizBowlUserService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
