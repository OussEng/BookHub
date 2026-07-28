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

  constructor(
    private authservice: AuthService,
    private router : Router,
  
  ) {}

  isAuthenticated(){
    return this.authservice.isAuthenticated()
  }

  logout(){
    this.authservice.logout()
    this.router.navigate(['/login']);
  }


}
