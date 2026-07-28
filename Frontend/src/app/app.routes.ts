import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { profilComponent } from './pages/profil/profil.component';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, title: 'Connexion', canActivate:[guestGuard] },
  { path: 'register', component: RegisterComponent, title: 'Inscription', canActivate:[guestGuard] },
  { path: 'profile', component: profilComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
