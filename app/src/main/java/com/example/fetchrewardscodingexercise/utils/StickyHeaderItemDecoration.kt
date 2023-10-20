package com.example.fetchrewardscodingexercise.utils

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fetchrewardscodingexercise.adapter.ItemAdapter
import com.example.fetchrewardscodingexercise.databinding.SectionHeaderBinding

/**
 * A [RecyclerView.ItemDecoration] that provides sticky headers functionality for
 *      Section headers. (listId), each of which sticks to the top of the RecyclerView's visible
 *      region until it's pushed off-screen by the next section's header.
 *
 * @property adapter Source Adapter from which this decoration will determine header positioning.
 */
class StickyHeaderItemDecoration(private val adapter: ItemAdapter) : RecyclerView.ItemDecoration() {

    /**
     * Draw any appropriate decorations into the Canvas supplied to the RecyclerView.
     * Decorations are drawn in the order they were added.
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val topChild = parent.getChildAt(0) ?: return
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) return

        // Get the header's position for the current top item.
        val sectionHeaderPos = getHeaderPositionForItem(topChildPosition)
        // Get the actual header view.
        val currentHeader = getHeaderViewForItem(sectionHeaderPos, parent)
        // Make sure the header's size is adjusted to fit within the RecyclerView.
        fixLayoutSize(parent, currentHeader)

        // Determine the point of contact for the header in the canvas.
        val contactPoint = currentHeader.bottom
        val childInContact = getChildInContact(parent, contactPoint, sectionHeaderPos)

        // Check if the child in contact is another header.
        if (childInContact != null && adapter.getItemViewType(
                parent.getChildAdapterPosition(
                    childInContact
                )
            ) == ItemAdapter.TYPE_SECTION
        ) {
            // Offset the current header upwards to make space for the next header.
            moveHeader(c, currentHeader, childInContact)
            return
        }

        // If no offset is needed, simply draw the header as is.
        drawHeader(c, currentHeader)
    }

    /**
     * Instantiates the header view for a given position.
     */
    private fun getHeaderViewForItem(headerPosition: Int, parent: RecyclerView): View {
        val header = adapter.onCreateViewHolder(parent, ItemAdapter.TYPE_SECTION).itemView
        val headerViewHolder = adapter.SectionViewHolder(SectionHeaderBinding.bind(header))
        adapter.onBindViewHolder(headerViewHolder, headerPosition)
        return header
    }

    /**
     * Offsets the current header upwards by the height difference between
     * it and the next header to smoothly transition between section headers.
     */
    private fun moveHeader(canvas: Canvas, currentHeader: View, nextHeader: View) {
        canvas.save()
        canvas.translate(0f, nextHeader.top - currentHeader.height.toFloat())
        currentHeader.draw(canvas)
        canvas.restore()
    }

    /**
     * Simply draws the header on the canvas at its current position.
     */
    private fun drawHeader(canvas: Canvas, header: View) {
        canvas.save()
        canvas.translate(0f, 0f)
        header.draw(canvas)
        canvas.restore()
    }

    /**
     * Returns the position of the header item in the adapter for the provided position.
     * It looks upwards in the adapter until it finds a header item and returns its position.
     */
    private fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = itemPosition
        do {
            if (adapter.getItemViewType(headerPosition) == ItemAdapter.TYPE_SECTION) {
                return headerPosition
            }
            headerPosition--
        } while (headerPosition >= 0)
        return 0 // This should never happen, but it's a safety net.
    }

    /**
     * Determines which visible child item is currently under the header.
     * It will find a child under the contact point which is not the header's immediate child.
     */
    private fun getChildInContact(
        parent: RecyclerView,
        contactPoint: Int,
        currentHeaderPos: Int
    ): View? {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            if (child.bottom > contactPoint && child.top <= contactPoint) {
                if (parent.getChildAdapterPosition(child) - currentHeaderPos != 1) {
                    return child
                }
            }
        }
        return null
    }

    /**
     * Adjusts the provided header view's width and height to ensure it fully fits within
     * the bounds of its parent RecyclerView.
     */
    private fun fixLayoutSize(parent: ViewGroup, view: View) {
        // Create width and height based on the parent's bounds.
        val widthSpec = View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        // Convert the width and height into child-specific measures.
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            view.layoutParams.height
        )

        // Set the size of the view by measuring and laying it out.
        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}
