import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { inject } from "@angular/core";




export const LibrarianGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLibrarian() || authService.isAdmin()) {
    return true;
  }

  router.navigate(['/']);
  return false;
};