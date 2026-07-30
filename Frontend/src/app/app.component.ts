import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBar } from './components/nav-bar/nav-bar';
import { Footer } from './components/footer/footer/footer';


@Component({
  selector: 'app-root',
  standalone: true,

  imports: [RouterOutlet, NavBar,Footer],
  templateUrl: './app.component.html'
})
export class AppComponent {}
