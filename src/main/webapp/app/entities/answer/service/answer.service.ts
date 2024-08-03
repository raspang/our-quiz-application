import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAnswer, NewAnswer } from '../answer.model';

export type PartialUpdateAnswer = Partial<IAnswer> & Pick<IAnswer, 'id'>;

export type EntityResponseType = HttpResponse<IAnswer>;
export type EntityArrayResponseType = HttpResponse<IAnswer[]>;

@Injectable({ providedIn: 'root' })
export class AnswerService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/answers');

  create(answer: NewAnswer): Observable<EntityResponseType> {
    return this.http.post<IAnswer>(this.resourceUrl, answer, { observe: 'response' });
  }

  update(answer: IAnswer): Observable<EntityResponseType> {
    return this.http.put<IAnswer>(`${this.resourceUrl}/${this.getAnswerIdentifier(answer)}`, answer, { observe: 'response' });
  }

  partialUpdate(answer: PartialUpdateAnswer): Observable<EntityResponseType> {
    return this.http.patch<IAnswer>(`${this.resourceUrl}/${this.getAnswerIdentifier(answer)}`, answer, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAnswer>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAnswer[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAnswerIdentifier(answer: Pick<IAnswer, 'id'>): number {
    return answer.id;
  }

  compareAnswer(o1: Pick<IAnswer, 'id'> | null, o2: Pick<IAnswer, 'id'> | null): boolean {
    return o1 && o2 ? this.getAnswerIdentifier(o1) === this.getAnswerIdentifier(o2) : o1 === o2;
  }

  addAnswerToCollectionIfMissing<Type extends Pick<IAnswer, 'id'>>(
    answerCollection: Type[],
    ...answersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const answers: Type[] = answersToCheck.filter(isPresent);
    if (answers.length > 0) {
      const answerCollectionIdentifiers = answerCollection.map(answerItem => this.getAnswerIdentifier(answerItem));
      const answersToAdd = answers.filter(answerItem => {
        const answerIdentifier = this.getAnswerIdentifier(answerItem);
        if (answerCollectionIdentifiers.includes(answerIdentifier)) {
          return false;
        }
        answerCollectionIdentifiers.push(answerIdentifier);
        return true;
      });
      return [...answersToAdd, ...answerCollection];
    }
    return answerCollection;
  }
}
