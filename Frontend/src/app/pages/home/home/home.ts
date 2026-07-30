import { Component, OnInit, signal } from '@angular/core';
import { BookService } from '../../../services/book-service/book.service';
import { Book } from '../../../interfaces/book/book';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  
  constructor(private bookService : BookService){}

  protected books = signal<Book[]>([]);
  protected isLoading = signal<boolean>(true);
  defaultCoverUrl = 'assets/images/default.png';


  
  protected currentPage = signal<number>(0);
  protected totalPages = signal<number>(0);
  protected totalElements = signal<number>(0);
  protected readonly pageSize = 20;

  
  protected searchInput = signal<string>('');
  protected selectedGenreId = signal<number | null>(null);
  protected sortBy = signal<string>('id');
  protected sortDir = signal<string>('desc');


  ngOnInit(): void {
    this.loadBooks(0);
  }

  loadBooks(page: number): void {
    this.isLoading.set(true);
    this.bookService
      .getBooks(
        page,
        this.pageSize,
        this.searchInput(),
        this.selectedGenreId() ?? undefined,
        this.sortBy(),
        this.sortDir()
      )
      .subscribe({
        next: (pageData) => {
          this.books.set(pageData.content);
          this.currentPage.set(pageData.number);
          this.totalPages.set(pageData.totalPages);
          this.totalElements.set(pageData.totalElements);
          this.isLoading.set(false);
        },
        error: (err) => {
          console.error('Error fetching books:', err);
          this.isLoading.set(false);
        },
      });
  }

  onSearch(query: string, genreVal: string, sortVal: string): void {
    this.searchInput.set(query);
    this.selectedGenreId.set(genreVal ? Number(genreVal) : null);

    if (sortVal) {
      const [field, dir] = sortVal.split(',');
      this.sortBy.set(field);
      this.sortDir.set(dir);
    }

    this.loadBooks(0);
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.loadBooks(page);
    }
  }

  handleImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = this.defaultCoverUrl;
  }


  get endIndex(): number {
    return Math.min((this.currentPage() + 1) * this.pageSize, this.totalElements());
  }


}
