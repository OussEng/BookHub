import { Author } from "../author/author";
import { BookDetail } from "../book-details/BookDetails";
import { Genre } from "../genre/genre";

export interface BookUpdateModalData {
  bookDetail: BookDetail;
  authors: Author[];
  genres: Genre[];
}