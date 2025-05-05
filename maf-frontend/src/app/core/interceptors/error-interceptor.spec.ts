import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { throwError, of } from 'rxjs';
import { errorInterceptor } from './error-interceptor';
import { TokenService } from '../services/token.service';
import { Router } from '@angular/router';

describe('errorInterceptor', () => {
  let tokenService: jasmine.SpyObj<TokenService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    const tokenServiceSpy = jasmine.createSpyObj('TokenService', ['signOut']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: TokenService, useValue: tokenServiceSpy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    tokenService = TestBed.inject(TokenService) as jasmine.SpyObj<TokenService>;
    router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
  });

  it('should handle 401 Unauthorized error', (done) => {
    // Arrange
    const req = new HttpRequest('GET', '/api/test');
    const errorResponse = new HttpErrorResponse({
      error: 'test 401 error',
      status: 401,
      statusText: 'Unauthorized'
    });

    const next: any = {
      handle: jasmine.createSpy('handle').and.returnValue(throwError(() => errorResponse))
    };

    // Act
    const result$ = errorInterceptor(req, next);

    // Assert
    result$.subscribe({
      error: (error) => {
        expect(tokenService.signOut).toHaveBeenCalled();
        expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
        done();
      }
    });
  });

  it('should pass through other errors', (done) => {
    // Arrange
    const req = new HttpRequest('GET', '/api/test');
    const errorResponse = new HttpErrorResponse({
      error: 'test 500 error',
      status: 500,
      statusText: 'Internal Server Error'
    });

    const next: any = {
      handle: jasmine.createSpy('handle').and.returnValue(throwError(() => errorResponse))
    };

    // Act
    const result$ = errorInterceptor(req, next);

    // Assert
    result$.subscribe({
      error: (error) => {
        expect(tokenService.signOut).not.toHaveBeenCalled();
        expect(router.navigate).not.toHaveBeenCalled();
        done();
      }
    });
  });
});
