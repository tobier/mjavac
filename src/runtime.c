/*
   A standard library for the MiniJava compiler project.  Garbage
   collection is not implemented. In addition to the procedures
   strictly needed for the project, the library also has a function
   that can be used to implement System.out.write().

   Original author Lars Engebretsen
   Modified by Torbjoern Granlund
   Modified by Tobias Eriksson
*/

#include <stdio.h>
#include <stdlib.h>

/*
   Allocates a class object on the heap.
   
   tobier: It also initializes all fields to 0.

   Param n: The total number of bytes required to store all field variables in
   the class object. Typically this is the machine word size times the number
   of field variables.

   Returns: A pointer to an object allocated on the heap.
*/
void *_minijavalib_allocate (size_t n)
{
	size_t* p = (size_t*)malloc(n);
	
	int i;
	for(i = 0; i < n; i++)
		p[i] = 0;

  return malloc (n);
}

/*
   Allocates an integer array on the heap an initializes all fields to zero and
   also initializes the length of the array.

   Param numberOfElements: The desired number of elements in the array.

   Returns: A pointer to array element zero. Given the returned pointer p and
   the machine word size W, element i is stored in memory location p+i*W and
   the length of the array is stored in memory location p-W.
*/
int *_minijavalib_initarray (int n_elem)
{
  int *a = _minijavalib_allocate ((n_elem + 1) * sizeof (int));
  int i;
  a[0] = n_elem;
  for (i = 1; i <= n_elem; i++)
    a[i] = 0;
  return &a[1];
}

/*
   Prints an integer to the standard output stream.

   Param i: The integer to be printed.
*/
void _minijavalib_println (int i)
{
  printf ("%d\n", i);
}

/*
   Prints a byte to the standard output stream.

   Param b: The character to be printed.
*/
void _minijavalib_writechar (int b)
{
  printf("%c", b);
}

/*
   Aborts due to array index out of bounds.

   Param l: The lower bound.
   Param b: The upper bound.
   Param i: The actual index.
*/
void _minijavalib_indexoutofbounds (int l, int b, int i)
{
  fprintf (stderr,
	   "Array index violates assertion: %d <= %d < %d\n",
	   l, i, b);
  exit (EXIT_FAILURE);
}

/*
   Aborts due to NULL pointer.
*/
void _minijavalib_nullpointertoobject ()
{
  fprintf (stderr, "Trying to dereference NULL pointer.\n");
  exit (EXIT_FAILURE);
}
