import {Injectable, SecurityContext} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root'
})
export class ErrorFormatterService {

  constructor(
    private domSanitizer: DomSanitizer,
  ) { }

  /**
   * Formats the error message received from the backend.
   *
   * This method sanitizes the error message to prevent any potentially dangerous HTML
   * content from being displayed. It also iterates over any additional error messages
   * provided in the 'errors' property of the error object and formats them accordingly.
   *
   * @param error The error object received from the backend.
   * @returns     A formatted error message with sanitized HTML content.
   */
  format(error: any): string {
    let message = this.domSanitizer.sanitize(SecurityContext.HTML, error.error.message) ?? '';
    if (!!error.error.errors) {
      message += ':<ul>';
      for (const e of error.error.errors) {
        /* Use Angular's DomSanitizer to strip dangerous parts out of the HTML
         * before putting it into the error message.
         * Toastr already does this, but it can't hurt to do here too,
         * in case the library every fails to do it.
         */
        const sanE = this.domSanitizer.sanitize(SecurityContext.HTML, e);
        message += `<li>${sanE}</li>`;
      }
      message += '</ul>';
    } else {
      message += '.';
    }
    return message;
  }
}
