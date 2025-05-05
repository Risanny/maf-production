import { TestBed } from '@angular/core/testing';
import { HttpRequest } from '@angular/common/http';
import { of } from 'rxjs';
import { authInterceptor } from './auth-interceptor';
import { TokenService } from '../services/token.service';

describe('authInterceptor', () => {
  let tokenService: jasmine.SpyObj<TokenService>;

  beforeEach(() => {
    const tokenServiceSpy = jasmine.createSpyObj('TokenService', ['getToken']);

    TestBed.configureTestingModule({
      providers: [
        { provide: TokenService, useValue: tokenServiceSpy }
      ]
    });

    tokenService = TestBed.inject(TokenService) as jasmine.SpyObj<TokenService>;
  });

  it('should add an Authorization header with token', () => {
    // Arrange
    const token = 'test-token';
    tokenService.getToken.and.returnValue(token);

    const req = new HttpRequest('GET', '/api/test');
    const next: any = {
      handle: jasmine.createSpy('handle').and.returnValue(of({}))
    };

    // Act
    authInterceptor(req, next);

    // Assert
    expect(next.handle).toHaveBeenCalled();
    const modifiedReq = next.handle.calls.first().args[0];
    expect(modifiedReq.headers.get('Authorization')).toEqual(`Bearer ${token}`);
  });

  it('should not modify request if token is null', () => {
    // Arrange
    tokenService.getToken.and.returnValue(null);

    const req = new HttpRequest('GET', '/api/test');
    const next: any = {
      handle: jasmine.createSpy('handle').and.returnValue(of({}))
    };

    // Act
    authInterceptor(req, next);

    // Assert
    expect(next.handle).toHaveBeenCalledWith(req);
  });
});
