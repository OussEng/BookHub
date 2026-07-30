import { Component, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterLink],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {


  isMobileMenuOpen = signal(false);
  isGestionDropdownOpen = signal(false);


  constructor(
    private authservice: AuthService,
    private router : Router,
  
  ) {}

  toggleMobileMenu(): void {
    this.isMobileMenuOpen.update(value => !value);
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen.set(false);
  }

  toggleGestionDropdown(): void {
    this.isGestionDropdownOpen.update(value => !value);
  }

  closeGestionDropdownWithDelay(): void {
    setTimeout(() => {
      this.isGestionDropdownOpen.set(false);
    }, 150);
  }

  isAuthenticated(){
    return this.authservice.isAuthenticated()
  }

  isLibrarian(){
    return this.authservice.isLibrarian()
  }

  logout(){
    this.authservice.logout()
    this.router.navigate(['/login']);
  }


}
