import { BookCondition } from './book-copy-condition';

export interface CreateBookCopyDto {
  serialNumber: string;
  condition: BookCondition | string;
  bookId: number;
}