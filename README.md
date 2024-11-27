# **Shoelace Theorem**:
- The Shoelace Theorem is used to calculate the area of the polygon:
  - Iterate over the list of points forming the edges of the fence.
  - For each point, calculate the determinant (partial area contribution) and sum up the results.
- The absolute value of half the summation gives the area.

# **Total Area**
- Computed area by *Shoelace Theorem* is only inside area of polygon. 
- It is necessary to add outside area (`(perimeter / 2) + 1`).