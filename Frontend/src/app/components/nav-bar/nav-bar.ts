import { Component, ElementRef, HostListener, inject, signal } from '@angular/core';
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

  private elementRef = inject(ElementRef);


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

  

  toggleGestionDropdown() {
  this.isGestionDropdownOpen.set(!this.isGestionDropdownOpen());
  }

  closeGestionDropdown() {
  this.isGestionDropdownOpen.set(false);
  } 

  @HostListener('document:click', ['$event'])
    onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.closeGestionDropdown();
    }

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
