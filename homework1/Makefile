PDF_NAME := HOMEWORK1
TEX_MAIN := main.tex

.PHONY: all pdf clean distclean

all: pdf

pdf: $(PDF_NAME).pdf

$(PDF_NAME).pdf: $(TEX_MAIN)
	pdflatex -interaction=nonstopmode -jobname=$(PDF_NAME) $(TEX_MAIN)
	pdflatex -interaction=nonstopmode -jobname=$(PDF_NAME) $(TEX_MAIN)

clean:
	rm -f $(PDF_NAME).aux $(PDF_NAME).bbl $(PDF_NAME).blg $(PDF_NAME).fdb_latexmk $(PDF_NAME).fls $(PDF_NAME).lof $(PDF_NAME).log $(PDF_NAME).lot $(PDF_NAME).out $(PDF_NAME).synctex.gz $(PDF_NAME).toc

distclean: clean
	rm -f $(PDF_NAME).pdf
