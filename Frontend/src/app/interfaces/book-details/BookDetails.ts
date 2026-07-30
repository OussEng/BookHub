export interface BookDetail {
  id: number;
  title: string;
  img: string;
  description: string;
  isbn: string;
  publishDate: string;
  authorIds: number[];
  genreIds: number[]; 
}