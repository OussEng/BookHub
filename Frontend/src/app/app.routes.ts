import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { profilComponent } from './pages/profil/profil.component';
import { authGuard } from './guards/auth.guard';
import { guestGuard } from './guards/guest.guard';
import { Home } from './pages/home/home/home';
import { BookDetails } from './pages/book-details/book-details';
import { LibrarianHome } from './pages/backoffice/librarian/librarian-home/librarian-home';
import { LibrarianGuard } from './guards/librarian.guard';
import { LibrarianBookCatalogue } from './pages/backoffice/librarian/librarian-book-catalogue/librarian-book-catalogue';
import { LibrarianBookCopies } from './pages/backoffice/librarian/librarian-book-copies/librarian-book-copies';

export const routes: Routes = [
  { path: '', component: Home, canActivate:[authGuard] },
  { path: 'login', component: LoginComponent, title: 'Connexion', canActivate:[guestGuard] },
  { path: 'register', component: RegisterComponent, title: 'Inscription', canActivate:[guestGuard] },
  { path: 'profile', component: profilComponent, canActivate: [authGuard] },
  { path: 'book/:id', component: BookDetails, canActivate: [authGuard] },
  { path: 'backoffice/librarian', component: LibrarianHome, title : 'Espace bibliothécaire', canActivate: [LibrarianGuard]},
  { path: 'backoffice/librarian/books', component: LibrarianBookCatalogue, title : 'Catalogue BookHub', canActivate: [LibrarianGuard]},
  { path: 'backoffice/librarian/books/copies/:id', component: LibrarianBookCopies, title : 'Catalogue BookHub', canActivate: [LibrarianGuard]},
  { path: '**', redirectTo: 'login' }
];
