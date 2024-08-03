import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IQuizBowlUser, NewQuizBowlUser } from '../quiz-bowl-user.model';

export type PartialUpdateQuizBowlUser = Partial<IQuizBowlUser> & Pick<IQuizBowlUser, 'id'>;

export type EntityResponseType = HttpResponse<IQuizBowlUser>;
export type EntityArrayResponseType = HttpResponse<IQuizBowlUser[]>;

@Injectable({ providedIn: 'root' })
export class QuizBowlUserService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/quiz-bowl-users');

  create(quizBowlUser: NewQuizBowlUser): Observable<EntityResponseType> {
    return this.http.post<IQuizBowlUser>(this.resourceUrl, quizBowlUser, { observe: 'response' });
  }

  update(quizBowlUser: IQuizBowlUser): Observable<EntityResponseType> {
    return this.http.put<IQuizBowlUser>(`${this.resourceUrl}/${this.getQuizBowlUserIdentifier(quizBowlUser)}`, quizBowlUser, {
      observe: 'response',
    });
  }

  partialUpdate(quizBowlUser: PartialUpdateQuizBowlUser): Observable<EntityResponseType> {
    return this.http.patch<IQuizBowlUser>(`${this.resourceUrl}/${this.getQuizBowlUserIdentifier(quizBowlUser)}`, quizBowlUser, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IQuizBowlUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IQuizBowlUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getQuizBowlUserIdentifier(quizBowlUser: Pick<IQuizBowlUser, 'id'>): number {
    return quizBowlUser.id;
  }

  compareQuizBowlUser(o1: Pick<IQuizBowlUser, 'id'> | null, o2: Pick<IQuizBowlUser, 'id'> | null): boolean {
    return o1 && o2 ? this.getQuizBowlUserIdentifier(o1) === this.getQuizBowlUserIdentifier(o2) : o1 === o2;
  }

  addQuizBowlUserToCollectionIfMissing<Type extends Pick<IQuizBowlUser, 'id'>>(
    quizBowlUserCollection: Type[],
    ...quizBowlUsersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const quizBowlUsers: Type[] = quizBowlUsersToCheck.filter(isPresent);
    if (quizBowlUsers.length > 0) {
      const quizBowlUserCollectionIdentifiers = quizBowlUserCollection.map(quizBowlUserItem =>
        this.getQuizBowlUserIdentifier(quizBowlUserItem),
      );
      const quizBowlUsersToAdd = quizBowlUsers.filter(quizBowlUserItem => {
        const quizBowlUserIdentifier = this.getQuizBowlUserIdentifier(quizBowlUserItem);
        if (quizBowlUserCollectionIdentifiers.includes(quizBowlUserIdentifier)) {
          return false;
        }
        quizBowlUserCollectionIdentifiers.push(quizBowlUserIdentifier);
        return true;
      });
      return [...quizBowlUsersToAdd, ...quizBowlUserCollection];
    }
    return quizBowlUserCollection;
  }
}
