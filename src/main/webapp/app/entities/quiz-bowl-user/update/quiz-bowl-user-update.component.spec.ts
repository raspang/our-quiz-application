import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient, HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { QuizBowlUserService } from '../service/quiz-bowl-user.service';
import { IQuizBowlUser } from '../quiz-bowl-user.model';
import { QuizBowlUserFormService } from './quiz-bowl-user-form.service';

import { QuizBowlUserUpdateComponent } from './quiz-bowl-user-update.component';

describe('QuizBowlUser Management Update Component', () => {
  let comp: QuizBowlUserUpdateComponent;
  let fixture: ComponentFixture<QuizBowlUserUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let quizBowlUserFormService: QuizBowlUserFormService;
  let quizBowlUserService: QuizBowlUserService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [QuizBowlUserUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(QuizBowlUserUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(QuizBowlUserUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    quizBowlUserFormService = TestBed.inject(QuizBowlUserFormService);
    quizBowlUserService = TestBed.inject(QuizBowlUserService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const quizBowlUser: IQuizBowlUser = { id: 456 };
      const user: IUser = { id: 9095 };
      quizBowlUser.user = user;

      const userCollection: IUser[] = [{ id: 32277 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ quizBowlUser });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const quizBowlUser: IQuizBowlUser = { id: 456 };
      const user: IUser = { id: 24741 };
      quizBowlUser.user = user;

      activatedRoute.data = of({ quizBowlUser });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.quizBowlUser).toEqual(quizBowlUser);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizBowlUser>>();
      const quizBowlUser = { id: 123 };
      jest.spyOn(quizBowlUserFormService, 'getQuizBowlUser').mockReturnValue(quizBowlUser);
      jest.spyOn(quizBowlUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizBowlUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quizBowlUser }));
      saveSubject.complete();

      // THEN
      expect(quizBowlUserFormService.getQuizBowlUser).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(quizBowlUserService.update).toHaveBeenCalledWith(expect.objectContaining(quizBowlUser));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizBowlUser>>();
      const quizBowlUser = { id: 123 };
      jest.spyOn(quizBowlUserFormService, 'getQuizBowlUser').mockReturnValue({ id: null });
      jest.spyOn(quizBowlUserService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizBowlUser: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: quizBowlUser }));
      saveSubject.complete();

      // THEN
      expect(quizBowlUserFormService.getQuizBowlUser).toHaveBeenCalled();
      expect(quizBowlUserService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IQuizBowlUser>>();
      const quizBowlUser = { id: 123 };
      jest.spyOn(quizBowlUserService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ quizBowlUser });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(quizBowlUserService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
